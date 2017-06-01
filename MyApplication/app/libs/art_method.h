/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef ART_RUNTIME_MIRROR_ART_METHOD_H_
#define ART_RUNTIME_MIRROR_ART_METHOD_H_

#include "modifiers.h"

namespace art {

    namespace mirror {
        class Object{
        public:
            uint32_t klass_;
            uint32_t monitor_;
            uint32_t entry_point_from_interpreter_;
            uint32_t entry_point_from_jni_;
            uint32_t entry_point_from_quick_compiled_code_;
        };
        class Class{
        public:
            uint32_t class_loader_;
            uint32_t clinit_thread_id_;
            uint32_t status_;
            uint32_t super_class_;
        };

// C++ mirror of java.lang.reflect.Method and java.lang.reflect.Constructor
        class ArtMethod : public Object {
        public:
            // for verifying offset information
            Class* declaring_class_;
            uint32_t dex_cache_resolved_types_;
            uint32_t access_flags_;
            uint32_t dex_cache_resolved_methods_;

            uint32_t dex_code_item_offset_;
            uint32_t method_index_;
            uint32_t dex_method_index_;

            Object ptr_sized_fields_;
        };
    }  // namespace mirror

}  // namespace art

#endif  // ART_RUNTIME_MIRROR_ART_METHOD_H_
