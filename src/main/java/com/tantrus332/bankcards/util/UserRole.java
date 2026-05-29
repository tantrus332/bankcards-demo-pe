package com.tantrus332.bankcards.util;

public enum UserRole {
  ADMIN,
  USER;

  // @com.fasterxml.jackson.annotation.JsonCreator
  // public static UserRole from(String value) {
  //     if(value == null) return null;
  //     try {
  //         return UserRole.valueOf(value.toUpperCase());
  //     } catch (IllegalArgumentException ex) {
  //         throw new IllegalArgumentException("Invalid role: " + value);
  //     }
  // }
}
