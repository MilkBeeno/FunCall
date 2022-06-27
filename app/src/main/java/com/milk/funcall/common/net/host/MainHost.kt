package com.milk.funcall.common.net.host

class MainHost : ApiHost {
    override fun releaseUrl(): String {
        return "http://funcall.sitepscodeserver.com"
    }

    override fun debugUrl(): String {
        return "http://funcall.sitepscodeserver.com"
    }
}