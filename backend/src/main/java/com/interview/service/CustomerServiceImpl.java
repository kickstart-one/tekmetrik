package com.interview.service;

import com.interview.model.Customer;
import com.interview.model.CustomerDTO;
import com.interview.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The service class implements the business logic for customer CRUD operations. The data model is not exposed beyond
 * this service layer, to ensure the HTTP controller only handles requests and responses.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CustomerDTO> getCustomers(int page, int size, String sortBy, String sortDirection) {

        Sort sort = Sort.by(Sort.Order.by(sortBy));

        // Choose sorting direction based on input
        sort = switch (sortDirection.toLowerCase()) {
            case "asc" -> sort.ascending();
            case "desc" -> sort.descending();
            default -> sort.ascending();
        };

        Pageable pageable = PageRequest.of(page, size, sort);
        return customerRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id).orElseThrow();
        customer.setEmail(customerDTO.getEmail());
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setAddress(customerDTO.getAddress());
        customer.setBirthYear(customerDTO.getBirthYear());

        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.findById(id).orElseThrow();
        customerRepository.deleteById(id);

    }

    private CustomerDTO convertToDTO(Customer customer) {
        return new CustomerDTO(customer.getId(), customer.getEmail(), customer.getFirstName(), customer.getLastName(),
                customer.getAddress(), customer.getBirthYear(), customer.getCreatedAt(), customer.getLastModifiedAt());
    }

    private Customer convertToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setEmail(customerDTO.getEmail());
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setAddress(customerDTO.getAddress());
        customer.setBirthYear(customerDTO.getBirthYear());
        customer.setId(customerDTO.getId());
        return customer;
    }
}
