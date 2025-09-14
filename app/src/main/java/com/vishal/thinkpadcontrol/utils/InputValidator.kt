package com.vishal.thinkpadcontrol.utils

object InputValidator {
    
    fun validateViewId(viewId: String): ValidationResult {
        return when {
            viewId.isBlank() -> ValidationResult.Invalid("View ID cannot be empty")
            viewId.length < Constants.VIEW_ID_MIN_LENGTH -> 
                ValidationResult.Invalid("View ID must be at least ${Constants.VIEW_ID_MIN_LENGTH} characters")
            viewId.length > Constants.VIEW_ID_MAX_LENGTH -> 
                ValidationResult.Invalid("View ID cannot exceed ${Constants.VIEW_ID_MAX_LENGTH} characters")
            !isValidViewIdFormat(viewId) -> 
                ValidationResult.Invalid("View ID must be in format 'com.package:id/view_name' or 'view_name'")
            else -> ValidationResult.Valid
        }
    }
    
    fun validateViewIdList(viewIds: List<String>): ValidationResult {
        if (viewIds.isEmpty()) {
            return ValidationResult.Invalid("At least one view ID is required")
        }
        
        if (viewIds.size > Constants.MAX_VIEW_IDS) {
            return ValidationResult.Invalid("Cannot have more than ${Constants.MAX_VIEW_IDS} view IDs")
        }
        
        viewIds.forEach { viewId ->
            val result = validateViewId(viewId)
            if (result is ValidationResult.Invalid) {
                return ValidationResult.Invalid("Invalid view ID '$viewId': ${result.message}")
            }
        }
        
        val duplicates = viewIds.groupBy { it }.filter { it.value.size > 1 }.keys
        if (duplicates.isNotEmpty()) {
            return ValidationResult.Invalid("Duplicate view IDs found: ${duplicates.joinToString(", ")}")
        }
        
        return ValidationResult.Valid
    }

    private fun isValidViewIdFormat(viewId: String): Boolean {
        return viewId.matches(Regex("^[a-zA-Z0-9_.]+(:id/[a-zA-Z0-9_]+)?$"))
    }
    
    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val message: String) : ValidationResult()
    }
}