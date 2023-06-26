package com.ghilly.service;

import com.ghilly.classes.Country;
import com.ghilly.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class CountryServiceRest implements CountryService {

    private static final Logger logger = LoggerFactory.getLogger(CountryServiceRest.class);
    private final CountryRepository repository;

    public CountryServiceRest(CountryRepository repository) {
        this.repository = repository;
    }


    @Override
    public void add(String countryName) {
        repository.insert(countryName);
        logger.info("The country {} was added by service.", countryName);
    }

    @Override
    public List<Country> getAllCountries() {
        logger.info("The list of countries is:");
        return repository.takeAllCountries();
    }

    @Override
    public Country getCountry(int countryId) {
        return repository.takeCountry(countryId);
    }

    @Override
    public void upgrade(int countryId, String newName) {
        if (!repository.containsCountry(countryId)) {
            throw new IllegalArgumentException("The country with the ID " + countryId + " is not found.");
        }
        String oldName = repository.takeCountry(countryId).getName();
        repository.update(countryId, newName);
        logger.info("The country with ID {} was upgraded, old name is {}, new name is {}.", countryId, oldName, newName);
    }

    @Override
    public void remove(int countryId) {
        if (!repository.containsCountry(countryId)) {
            throw new IllegalArgumentException("The country with the ID " + countryId + " is not found.");
        }
        repository.delete(countryId);
        logger.info("The country with ID {} was deleted", countryId);
    }
}
