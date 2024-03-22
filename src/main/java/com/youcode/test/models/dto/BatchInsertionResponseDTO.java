package com.youcode.test.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchInsertionResponseDTO {
    private int successfullyInsertedRows;
    private int failedToInsertRows;
}
