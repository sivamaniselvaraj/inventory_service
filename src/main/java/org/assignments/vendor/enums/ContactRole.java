package org.assignments.vendor.enums;

/**
 * Role / function of a vendor contact person.
 * One vendor may have contacts across multiple roles.
 */
public enum ContactRole {
    PRIMARY,          // Main day-to-day point of contact
    SALES,            // Sales / account manager
    PROCUREMENT,      // Purchase orders and sourcing
    ACCOUNTS,         // Billing, invoices, payments
    LOGISTICS,        // Shipping, delivery, tracking
    TECHNICAL,        // Product quality / technical support
    ESCALATION,       // Escalation / senior management
    EMERGENCY         // 24x7 emergency / critical issues
}
