#!/usr/bin/env groovy

/*
 * 'ERROR: Library global-lib expected to contain at least one of src or vars directories' 에러 방지 위한 fake groovy
 */

def call() {
  cho "WHY ARE YOU CALLING THIS FUNCTION!??!"
}