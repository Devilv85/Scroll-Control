package com.vishal.thinkpadcontrol.utils

/**
 * Utility class for validating user inputs throughout the application
 */
object InputValidator {
    
    /**
     * Validates view ID input for custom detection patterns
     */
    fun validateViewId(viewId: String): ValidationResult {
        return when {
            viewId.isBlank() -> ValidationResult.Invalid("View ID cannot be empty")
            viewId.length < 3 -> ValidationResult.Invalid("View ID must be at least 3 characters")
            viewId.length > 100 -> ValidationResult.Invalid("View ID cannot exceed 100 characters")
            !viewId.matches(Regex("^[a-zA-Z0-9_]+$")) -> 
                ValidationResult.Invalid("View ID can only contain letters, numbers, and underscores")
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Validates a list of view IDs
     */
    fun validateViewIdList(viewIds: List<String>): ValidationResult {
        if (viewIds.isEmpty()) {
            return ValidationResult.Invalid("At least one view ID is required")
        }
        
        if (viewIds.size > 50) {
            return ValidationResult.Invalid("Cannot have more than 50 view IDs")
        }
        
        viewIds.forEach { viewId ->
            val result = validateViewId(viewId)
            if (result is ValidationResult.Invalid) {
                return ValidationResult.Invalid("Invalid view ID '$viewId': ${result.message}")
            }
        }
        
        // Check for duplicates
        val duplicates = viewIds.groupBy { it }.filter { it.value.size > 1 }.keys
        if (duplicates.isNotEmpty()) {
            return ValidationResult.Invalid("Duplicate view IDs found: ${duplicates.joinToString(", ")}")
        }
        
        return ValidationResult.Valid
    }
    
    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val message: String) : ValidationResult()
    }
}