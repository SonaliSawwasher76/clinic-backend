package com.clinic.specification;

import com.clinic.entity.Doctor;
import com.clinic.entity.user.User;
import com.clinic.entity.user.UserProfile;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> byWorkspaceId(Long workspaceId) {
        return (root, query, criteriaBuilder) -> {
            if (workspaceId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("workspace").get("workspaceId"), workspaceId);
        };
    }

    public static Specification<User> byRole(String role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null || role.equalsIgnoreCase("all")) {
                return criteriaBuilder.conjunction();
            }

            if (role.equalsIgnoreCase("doctor")) {
                // Subquery to check if User is a doctor
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Doctor> doctorRoot = subquery.from(Doctor.class);
                subquery.select(doctorRoot.get("user").get("userId"))
                        .where(criteriaBuilder.equal(doctorRoot.get("user").get("userId"), root.get("userId")));

                return criteriaBuilder.exists(subquery);
            } else {
                // Filter by role field for other roles
                return criteriaBuilder.equal(root.get("role"), role.toUpperCase());
            }
        };
    }

    public static Specification<User> searchByText(String searchText) {
        return (root, query, criteriaBuilder) -> {
            if (searchText == null || searchText.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likePattern = "%" + searchText.toLowerCase() + "%";

            Join<User, UserProfile> profileJoin = root.join("userProfile", JoinType.LEFT);

            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(profileJoin.get("firstName")), likePattern);
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(profileJoin.get("lastName")), likePattern);
            Predicate addressPredicate = criteriaBuilder.like(criteriaBuilder.lower(profileJoin.get("address")), likePattern);
            Predicate contactPredicate = criteriaBuilder.like(criteriaBuilder.lower(profileJoin.get("contactNo")), likePattern);
            Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern);

            return criteriaBuilder.or(firstNamePredicate, lastNamePredicate, addressPredicate, contactPredicate, emailPredicate);
        };
    }
}
