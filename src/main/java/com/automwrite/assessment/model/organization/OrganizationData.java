package com.automwrite.assessment.model.organization;

import lombok.Data;
import java.util.List;

@Data
public class OrganizationData {
    private OrganizationInfo organizationInfo;

    @Data
    public class OrganizationInfo {
        private OrganizationDetails organizationDetails;
        private Platforms platforms;
        private ServiceProposition serviceProposition;
        private Fees fees;
    }

    @Data
    public class OrganizationDetails {
        private String organizationName;
        private String websiteUrl;
        private String email;
        private String phoneNumber;
        private Address address;
    }

    @Data
    public class Address {
        private String street;
        private String city;
        private String postcode;
        private String country;
    }

    @Data
    public class Platforms {
        private List<Platform> items;
    }

    @Data
    public class Platform {
        private String name;
        private String description;
        private Fees fees;
        private List<String> features;
    }

    @Data
    public class Fees {
        private List<StructuredCharge> structuredCharge;
    }

    @Data
    public class StructuredCharge {
        private String startAmount;
        private String endAmount;
        private String fee;
    }

    @Data
    public class ServiceProposition {
        private String adviceType;
        private List<Portfolio> investmentPortfolios;
    }

    @Data
    public class Portfolio {
        private String name;
        private String description;
        private int riskLevel;
        private TargetAllocation targetAllocation;
        private AnnualManagementCharge annualManagementCharge;
        private long minimumInvestment;
    }

    @Data
    public class TargetAllocation {
        private int bonds;
        private int equities;
        private int cash;
        private int alternatives;
    }

    @Data
    public class AnnualManagementCharge {
        private String percentage;
    }

    @Data
    public class OrganizationFees {
        private Fee initialAdviceFee;
        private Fee ongoingAdviceFee;
        private Fee platformFee;
    }

    @Data
    public class Fee {
        private String percentage;
        private String description;
    }
}
