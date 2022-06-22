package com.milk.funcall.common.paging.status

/**
 * 下拉刷新的状态 Success 表示 网络请求成功并且数据不为空
 *             Empty   表示 网络请求成功但数据为空
 *             Error   表示 网络请求失败且列表为空、通常是第一次刷新
 *             Failed  表示 网络请求失败但列表不为空、通常是第一次网络获取了数据、或者数据库中保存有数据
 */
enum class RefreshStatus { Success, Empty, Error, Failed }