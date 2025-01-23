package com.automwrite.assessment.model.client;

import lombok.Data;

@Data
public class ClientData {
    private ClientInfo clientInfo;
    private PensionPlans pensionPlans;
    private String lastUpdated;
    private AdvisorDetails advisorDetails;

    @Data
    public class ClientInfo {
        private String id;
        private PersonalDetails personalDetails;
        private Address address;
        private EmploymentStatus employmentStatus;
        private FinancialProfile financialProfile;
    }

    @Data
    public class PersonalDetails {
        private String firstName;
        private String lastName;
        private String dateOfBirth;
        private String email;
        private String phone;
    }

    @Data
    public class Address {
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
    }

    @Data
    public class EmploymentStatus {
        private String status;
        private String employer;
        private long annualIncome;
        private int yearsWithEmployer;
    }

    @Data
    public class FinancialProfile {
        private String riskTolerance;
        private String investmentHorizon;
        private long liquidAssets;
        private long totalNetWorth;
        private double monthlyExpenses;
        private OutstandingDebts outstandingDebts;
    }

    @Data
    public class OutstandingDebts {
        private long mortgage;
        private long carLoan;
        private long creditCards;
    }

    @Data
    public class PensionPlans {
        private GuaranteedPlan guaranteedPlan;
        private MarketBasedPlan marketBasedPlan;
    }

    @Data
    public class GuaranteedPlan {
        private String planId;
        private String provider;
        private String planType;
        private long planValue;
        private GuaranteedBenefits guaranteedBenefits;
        private String vestingStatus;
        private String normalRetirementDate;
    }

    @Data
    public class GuaranteedBenefits {
        private double monthlyPension;
        private String guaranteedPeriod;
        private double survivalBenefit;
        private InflationProtection inflationProtection;
    }

    @Data
    public class InflationProtection {
        private String type;
        private double rate;
    }

    @Data
    public class MarketBasedPlan {
        private String planId;
        private String provider;
        private String planType;
        private long planValue;
        private Investments investments;
        private ProjectedValues projectedValues;
        private ContributionDetails contributionDetails;
    }

    @Data
    public class Investments {
        private int equities;
        private int bonds;
        private int cash;
    }

    @Data
    public class ProjectedValues {
        private long conservative;
        private long moderate;
        private long aggressive;
    }

    @Data
    public class ContributionDetails {
        private double employeeContribution;
        private double employerMatch;
        private double annualTotal;
    }

    @Data
    public class AdvisorDetails {
        private String name;
        private String id;
        private String phone;
        private String email;
    }
}
