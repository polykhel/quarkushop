package com.polykhel.quarkushop.service;

import com.polykhel.quarkushop.domain.Cart;
import com.polykhel.quarkushop.domain.enums.CartStatus;
import com.polykhel.quarkushop.dto.CartDto;
import com.polykhel.quarkushop.repository.CartRepository;
import com.polykhel.quarkushop.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class CartService {

    CartRepository cartRepository;

    CustomerRepository customerRepository;

    public CartService(CartRepository cartRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
    }

    public static CartDto mapToDto(Cart cart) {
        return new CartDto(cart.getId(), CustomerService.mapToDto(cart.getCustomer()), cart.getStatus().name());
    }

    public List<CartDto> findAll() {
        log.debug("Requet to get all Carts");
        return this.cartRepository.findAll().stream().map(CartService::mapToDto).collect(Collectors.toList());
    }

    public List<CartDto> findAllActiveCarts() {
        return this.cartRepository.findByStatus(CartStatus.NEW).stream().map(CartService::mapToDto).collect(Collectors.toList());
    }

    public Cart create(Long customerId) {
        if (this.getActiveCart(customerId) == null) {
            var customer = this.customerRepository.findById(customerId).orElseThrow(() -> new IllegalStateException("The Customer does not exist!"));

            var cart = new Cart(customer, CartStatus.NEW);

            return this.cartRepository.save(cart);
        } else {
            throw new IllegalStateException("There is already an active cart");
        }
    }

    public CartDto createDto(Long customerId) {
        return mapToDto(this.create(customerId));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public CartDto findById(Long id) {
        log.debug("Request to get Cart : {}", id);
        return this.cartRepository.findById(id).map(CartService::mapToDto).orElse(null);
    }

    public void delete(Long id) {
        log.debug("Request to delete Cart : {}", id);
        Cart cart = this.cartRepository.findById(id).orElseThrow(() -> new IllegalStateException("Cannot find cart with id " + id));

        cart.setStatus(CartStatus.CANCELED);

        this.cartRepository.save(cart);
    }

    public CartDto getActiveCart(Long customerId) {
        List<Cart> carts = this.cartRepository.findByStatusAndCustomerId(CartStatus.NEW, customerId);
        if (carts != null) {
            if (carts.size() == 1) {
                return mapToDto(carts.get(0));
            }
            if (carts.size() > 1) {
                throw new IllegalStateException("Many active carts detected !!!");
            }
        }
        return null;
    }


}
