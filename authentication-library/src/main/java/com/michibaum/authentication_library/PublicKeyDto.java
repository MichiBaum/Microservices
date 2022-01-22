package com.michibaum.authentication_library;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicKeyDto {

    private String algorithm;
    private byte[] key;

}
