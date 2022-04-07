package com.polykhel.quarkushop.web;

import io.quarkus.test.junit.NativeImageTest;

import javax.sql.DataSource;

@NativeImageTest
class CartResourceIT extends CartResourceTest {

    public CartResourceIT(DataSource datasource) {
        super(datasource);
    }
}
