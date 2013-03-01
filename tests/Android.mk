
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# We only want this apk build for tests.
LOCAL_MODULE_TAGS := tests

LOCAL_JAVA_LIBRARIES := android.test.runner

# Include all test java files.
LOCAL_SRC_FILES := $(call all-java-files-under, src)

# Notice that we don't have to include the src files of Browser because, by
# running the tests using an instrumentation targeting Browser, we
# automatically get all of its classes loaded into our environment.

LOCAL_PACKAGE_NAME := BrowserTests

LOCAL_INSTRUMENTATION_FOR := Browser

include $(BUILD_PACKAGE)
