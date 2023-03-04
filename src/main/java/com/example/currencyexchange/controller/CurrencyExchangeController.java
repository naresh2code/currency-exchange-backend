package com.example.currencyexchange.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.currencyexchange.model.ConversionResult;
import com.example.currencyexchange.model.ExchangeRate;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class CurrencyExchangeController {

    
    @GetMapping("/currency-exchange")
    public List<ExchangeRate> getExchangeRates( @RequestParam("from") String fromCurrency,
            @RequestParam("to") String toCurrency) throws Exception{
    	
    	List<ExchangeRate> exchangeRates = new ArrayList<>(); 
    	HttpClient httpClient = HttpClient.newHttpClient();
    	String apiLayerKey = "cxmUJp4gfUP7uJ7XT9mcXUzTfC68yMMQ";
        
        //1. Fetch data from Currency Layer API
        String currencyLayerApiUrl =String.format("https://api.apilayer.com/currency_data/convert?to=%s&from=%s&amount=1&apikey=%s", toCurrency, fromCurrency, apiLayerKey);
        HttpRequest currencyLayerRequest = HttpRequest.newBuilder()
                .uri(URI.create(currencyLayerApiUrl))
                .GET()
                .build();
        
        HttpResponse<String> currencyLayerResponse = httpClient.send(currencyLayerRequest, HttpResponse.BodyHandlers.ofString());
        String currencyLayerData = currencyLayerResponse.body();
        System.out.println("Currency Layer API data: " + currencyLayerData); 
       
        JSONObject currencyDataJson=new JSONObject(currencyLayerData);
        System.out.print("result:"+currencyDataJson.getDouble("result"));
        
        ExchangeRate currencyExchangeData= new ExchangeRate();
        currencyExchangeData.setExchange_rate(currencyDataJson.getDouble("result"));
        currencyExchangeData.setSource("https://currencylayer.com/");
        exchangeRates.add(currencyExchangeData);
        
        //2. Fetch data from Fixer.io API
        String fixerApiUrl =String.format("https://api.apilayer.com/fixer/convert?to=%s&from=%s&amount=1&apikey=%s", toCurrency, fromCurrency, apiLayerKey);
        HttpRequest fixerRequest = HttpRequest.newBuilder()
                .uri(URI.create(fixerApiUrl))
                .GET()
                .build();
        
        HttpResponse<String> fixerResponse = httpClient.send(fixerRequest, HttpResponse.BodyHandlers.ofString());
        String fixerData = fixerResponse.body();
        System.out.println("fixer.io API data: " + fixerData);
        
        JSONObject fixerDataJson=new JSONObject(fixerData);
        System.out.print("result:"+fixerDataJson.getDouble("result"));
        
        ExchangeRate fixerExchangeData= new ExchangeRate();
        fixerExchangeData.setExchange_rate(fixerDataJson.getDouble("result"));
        fixerExchangeData.setSource("https://fixer.io/");
        exchangeRates.add(fixerExchangeData);
        
        //3. Fetch data from Exchange Rates API
        String exchangeRatesApiUrl =String.format("https://api.apilayer.com/exchangerates_data/convert?to=%s&from=%s&amount=1&apikey=%s", toCurrency, fromCurrency, apiLayerKey);
        HttpRequest exchangeRatesRequest = HttpRequest.newBuilder()
                .uri(URI.create(exchangeRatesApiUrl))
                .GET()
                .build();
        
        HttpResponse<String> exchangeRatesResponse = httpClient.send(exchangeRatesRequest, HttpResponse.BodyHandlers.ofString());
        String exchangeRatesData = exchangeRatesResponse.body();
        System.out.println("fixer.io API data: " + fixerData);
        
        JSONObject exchangeRatesDataJson=new JSONObject(exchangeRatesData);
        System.out.print("result:"+fixerDataJson.getDouble("result"));
        
        ExchangeRate exchangeData= new ExchangeRate();
        exchangeData.setExchange_rate(exchangeRatesDataJson.getDouble("result"));
        exchangeData.setSource("https://exchangeratesapi.io/");
        exchangeRates.add(exchangeData);

        return exchangeRates;
    }
    
    
    @GetMapping("/convert")
    public ConversionResult convert(
            @RequestParam("from") String fromCurrency,
            @RequestParam("to") String toCurrency,
            @RequestParam("amount") double amount) throws Exception {
    	
        List<ExchangeRate> exchangeRates = getExchangeRates(fromCurrency, toCurrency);
        double maxValue = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;
        for (ExchangeRate exchangeRate : exchangeRates) {
            double value = amount * exchangeRate.getExchange_rate();
            if (value > maxValue) {
                maxValue = value;
            }
            if (value < minValue) {
                minValue = value;
            }
        }
        return new ConversionResult(maxValue, minValue);
    }
}
