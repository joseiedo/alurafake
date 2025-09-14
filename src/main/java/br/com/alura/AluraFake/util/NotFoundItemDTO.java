package br.com.alura.AluraFake.util;

import org.springframework.util.Assert;

public class NotFoundItemDTO {
    private final String message;

    public NotFoundItemDTO(Class<?> entityClass) {
        Assert.notNull(entityClass, "entityClass must not be null");
        this.message = "%s not found".formatted(entityClass.getSimpleName());
    }

    public String getMessage() {
        return message;
    }
}
