package com.polykhel.service;

import com.polykhel.domain.Address;
import com.polykhel.dto.AddressDto;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AddressService {
    public static Address createFromDto(AddressDto addressDto) {
        return new Address(
                addressDto.getAddress1(),
                addressDto.getAddress2(),
                addressDto.getCity(),
                addressDto.getPostcode(),
                addressDto.getCountry()
        );
    }

    public static AddressDto mapToDto(Address address) {
        return new AddressDto(
                address.getAddress1(),
                address.getAddress2(),
                address.getCity(),
                address.getPostcode(),
                address.getCountry()
        );
    }
}
