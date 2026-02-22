package com.sanjayrisbud.starzzboot.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final Integer resourceId;

    public ResourceNotFoundException(String resourceName, Integer resourceId) {
        super(resourceName + " with id " + resourceId + " not found.");
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

}