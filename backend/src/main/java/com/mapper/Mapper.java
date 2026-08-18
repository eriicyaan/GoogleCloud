package com.mapper;

public interface Mapper<FROM, TO> {

    TO map(FROM object);

}
