/*
 * Copyright 2016 Crown Copyright
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

package stroom.task.client;

import com.google.gwt.core.client.GWT;

public final class TaskCount {

    private static int count;

    private TaskCount() {
    }

    public static int increment() {
        count++;
        return count;
    }

    public static int decrement() {
        count--;

        if (count < 0) {
            GWT.log("Negative task count");
        }

        return count;
    }
}
