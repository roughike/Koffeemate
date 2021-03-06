/*
 * Copyright 2017 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.koffeemate.data.models

import io.realm.RealmObject

open class Profile(
        open var first_name: String = "",
        open var last_name: String = "",
        open var real_name: String = "",
        open var image_72: String? = null,
        open var image_192: String? = null,
        open var image_512: String? = null
) : RealmObject() {
    val largestAvailableImage: String
        get() = image_512 ?: image_192 ?: image_72 ?: ""

    val smallestAvailableImage: String
        get() = image_72 ?: image_192 ?: image_512 ?: ""
}
