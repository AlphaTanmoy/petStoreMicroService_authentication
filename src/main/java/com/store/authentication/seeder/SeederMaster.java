package com.store.authentication.seeder;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeederMaster {

    @Autowired
    private SeedDataToAuthUsers seedDataToAuthUsers;

    public SeederMaster(SeedDataToAuthUsers seedDataToAuthUsers) {
        this.seedDataToAuthUsers = seedDataToAuthUsers;
    }

    @PostConstruct
    public void StartSeeding(){
        System.out.println("Start Seeding....");
        _SeedDataToAuthUsers();
        System.out.println("Seeding Completed");
    }

    private void _SeedDataToAuthUsers(){
        System.out.println("Going to Seed Master User");
        seedDataToAuthUsers.seedAuthMasterData();
        System.out.println("Master User Seeded Successfully");
    }
}
