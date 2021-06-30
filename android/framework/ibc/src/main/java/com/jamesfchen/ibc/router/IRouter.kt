package com.jamesfchen.ibc.router

import android.content.Context
import android.os.Bundle

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 *
 *
 * scheme://bundle/page?param1=value1&param2=value2 ...
 */
interface IRouter {
    fun call(cxt: Context, bundle: Bundle)
}