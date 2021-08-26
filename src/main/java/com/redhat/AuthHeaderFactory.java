package com.redhat;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import java.nio.file.Files;
import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class AuthHeaderFactory implements ClientHeadersFactory {

    @ConfigProperty(name = "accessToken") 
    String apiToken;

    @ConfigProperty(name = "accessTokenFile")
    String accessTokenFile;
    

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        MultivaluedMap<String, String> result = new MultivaluedMapImpl<>();
        if("my-access-token".equalsIgnoreCase(apiToken)){
            System.out.println("TOKEN not found ");
            if(accessTokenFile!=null && accessTokenFile.trim().length()>0){
                try {
                    System.out.println("Using token from " + accessTokenFile);
                    String s = Files.readString(new File(accessTokenFile).toPath());
                    System.out.println("Using token : " + s);
                    result.add("Authorization", "Bearer " + s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            System.out.println("Using token : " + apiToken);
            result.add("Authorization", "Bearer " + apiToken);
        }
        
        return result;
    }
}