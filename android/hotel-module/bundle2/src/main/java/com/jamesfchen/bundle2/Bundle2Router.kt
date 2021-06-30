package com.jamesfchen.bundle2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jamesfchen.ibc.router.IRouter
import com.jamesfchen.bundle2.SayHiActivity
/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author: jamesfchen
 * @email: hawksjamesf@gmail.com
 * @since: 六月/30/2021  星期三
 */
class Bundle2Router : IRouter {
    override fun call(cxt: Context,bundle:Bundle) {
        cxt.startActivity(Intent(cxt,SayHiActivity::class.java))
    }
}