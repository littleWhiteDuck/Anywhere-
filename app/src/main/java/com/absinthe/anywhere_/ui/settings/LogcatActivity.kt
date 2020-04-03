package com.absinthe.anywhere_.ui.settings

import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.log.LogAdapter
import com.absinthe.anywhere_.adapter.log.LogDiffCallback
import com.absinthe.anywhere_.databinding.ActivityLogcatBinding
import com.absinthe.anywhere_.model.GlobalValues
import com.absinthe.anywhere_.model.LogModel
import com.absinthe.anywhere_.utils.AppUtils.sendLogcat
import com.absinthe.anywhere_.utils.AppUtils.startLogcat
import com.absinthe.anywhere_.utils.NotifyUtils
import com.absinthe.anywhere_.utils.manager.LogRecorder
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.NotificationUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class LogcatActivity : BaseActivity() {

    private lateinit var mBinding: ActivityLogcatBinding
    private var mAdapter: LogAdapter = LogAdapter()

    init {
        isPaddingToolbar = true
    }

    override fun setViewBinding() {
        mBinding = ActivityLogcatBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar
    }

    override fun onDestroy() {
        GlobalValues.sIsDebugMode = false
        super.onDestroy()
    }

    override fun initView() {
        super.initView()
        mBinding.rvLog.layoutManager = LinearLayoutManager(this)
        mAdapter.setDiffCallback(LogDiffCallback())
        mBinding.rvLog.adapter = mAdapter

        mAdapter.setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
            val logModel = adapter.getItem(position) as LogModel?

            if (logModel != null) {
                if (view.id == R.id.btn_delete) {
                    if (FileUtils.delete(logModel.filePath)) {
                        mAdapter.remove(position)
                    }
                } else if (view.id == R.id.btn_send) {
                    val file = FileUtils.getFileByPath(logModel.filePath)
                    sendLogcat(this, file)
                }
            }
        }
        if (isStartCatching) {
            mBinding.btnLogcat.text = getText(R.string.btn_stop_catch_log)
        } else {
            mBinding.btnLogcat.text = getText(R.string.btn_start_catch_log)
        }
        mBinding.btnLogcat.setOnClickListener {
            if (isStartCatching) {
                mBinding.btnLogcat.text = getText(R.string.btn_start_catch_log)
                isStartCatching = false
                LogRecorder.getInstance().stop()
                NotificationUtils.cancel(NotifyUtils.LOGCAT_NOTIFICATION_ID)

                GlobalScope.launch(Dispatchers.IO) {
                    delay(100)
                    initData(true)
                }
            } else {
                mBinding.btnLogcat.text = getText(R.string.btn_stop_catch_log)
                isStartCatching = true
                startLogcat(this@LogcatActivity)
            }
        }
        initData(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initData(isRefresh: Boolean) {
        val list: MutableList<LogModel> = ArrayList()
        val file = getExternalFilesDir(getString(R.string.logcat))

        if (FileUtils.isFileExists(file)) {
            val fileList = FileUtils.listFilesInDir(file) { o1: File, o2: File ->
                - o1.lastModified().toString().compareTo(o2.lastModified().toString())
            }

            if (fileList != null) {
                for (logFile in fileList) {
                    val date = Date(logFile.lastModified()).toLocaleString()
                    val logModel = LogModel().apply {
                        createTime = date
                        filePath = logFile.absolutePath
                        fileSize = logFile.length()
                    }
                    list.add(logModel)
                }
                if (isRefresh) {
                    mAdapter.setDiffNewData(list)
                } else {
                    mAdapter.setNewData(list)
                }
            }
        }
    }

    companion object {
        var isStartCatching = false
    }
}