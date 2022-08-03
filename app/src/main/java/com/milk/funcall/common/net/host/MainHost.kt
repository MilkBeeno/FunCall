package com.milk.funcall.common.net.host

class MainHost : ApiHost {
    override fun releaseUrl(): String {
        return "https://api.simplefuncall.com"
    }

    override fun debugUrl(): String {
        return "http://funcall.sitepscodeserver.com"
    }
}