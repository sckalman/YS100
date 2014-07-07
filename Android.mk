#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 SpeechApi mina-core-2.0.7 nineoldandroids-2.4.0  slf4j-api-1.7.7  slf4j-jdk14-1.7.7

LOCAL_SRC_FILES := $(call all-java-files-under, src)

#LOCAL_SDK_VERSION := current
LOCAL_CERTIFICATE := platform

LOCAL_PACKAGE_NAME := testUsbConnect

include $(BUILD_PACKAGE)
##################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := SpeechApi:libs/SpeechApi.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += mina-core-2.0.7:libs/mina-core-2.0.7.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += nineoldandroids-2.4.0:libs/nineoldandroids-2.4.0.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += slf4j-api-1.7.7:libs/slf4j-api-1.7.7.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += slf4j-jdk14-1.7.7:libs/slf4j-jdk14-1.7.7.jar

include $(BUILD_MULTI_PREBUILT)

LOCAL_CERTIFICATE := key

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
