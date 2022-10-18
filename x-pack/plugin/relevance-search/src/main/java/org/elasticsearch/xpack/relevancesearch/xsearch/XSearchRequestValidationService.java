/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.relevancesearch.xsearch;

import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.ValidationException;
import org.elasticsearch.xpack.relevancesearch.xsearch.action.XSearchAction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XSearchRequestValidationService {

    private final IndexNameExpressionResolver indexNameExpressionResolver;
    private final ClusterService clusterService;

    public XSearchRequestValidationService(IndexNameExpressionResolver indexNameExpressionResolver, ClusterService clusterService) {
        this.indexNameExpressionResolver = indexNameExpressionResolver;
        this.clusterService = clusterService;
    }

    public void validateRequest(XSearchAction.Request request) throws ValidationException {

        String[] indices = request.indices();
        Set<String> engines = new HashSet<>(
            indexNameExpressionResolver.searchEngineNames(clusterService.state(), request.indicesOptions(), indices)
        );

        List<String> invalidIndices = Arrays.stream(indices).filter(index -> engines.contains(index) == false).toList();
        if (invalidIndices.size() > 0) {
            ValidationException e = new ValidationException();
            e.addValidationError("XSearch not supported for non-engine indices " + String.join(",", invalidIndices));
            throw e;
        }
    }

}
