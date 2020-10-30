package com.absinthe.anywhere_.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.api.ApiManager
import com.absinthe.anywhere_.api.GitHubApi
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.databinding.LayoutCloudRuleDetailBinding
import com.absinthe.anywhere_.model.cloud.RuleEntity
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.database.PageEntity
import com.absinthe.anywhere_.utils.CipherUtils
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

const val EXTRA_URL = "EXTRA_URL"

class CloudRuleDetailDialogFragment : AnywhereDialogFragment() {

    private val url by lazy { arguments?.getString(EXTRA_URL) ?: "" }
    private val viewModel by viewModels<AnywhereViewModel>()
    private var entity: AnywhereEntity? = null
    private lateinit var binding: LayoutCloudRuleDetailBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = LayoutCloudRuleDetailBinding.inflate(layoutInflater)
        init()
        return AnywhereDialogBuilder(requireContext()).setView(binding.root).create()
    }

    private fun init() {
        val retrofit = Retrofit.Builder()
                .baseUrl(ApiManager.GITEE_RULES_RAW_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val request = retrofit.create(GitHubApi::class.java)
        val task = request.requestEntity(url.removePrefix(ApiManager.GITEE_RULES_RAW_URL))
        task.enqueue(object : Callback<RuleEntity> {
            override fun onResponse(call: Call<RuleEntity>, response: Response<RuleEntity>) {
                Timber.d("onResponse")
                response.body()?.let { ruleEntity ->
                    val decrypted = CipherUtils.decrypt(ruleEntity.content)
                    entity = Gson().fromJson(decrypted, AnywhereEntity::class.java)
                    binding.tvName.text = ruleEntity.name
                    binding.tvContributor.text = ruleEntity.contributor
                    binding.tvDesc.text = ruleEntity.desc
                    binding.btnAdd.setOnClickListener {
                        entity?.let {
                            lifecycleScope.launch(Dispatchers.IO) {
                                if (AnywhereApplication.sRepository.getPageEntityByTitle(it.category) == null) {
                                    val category = it.category.ifEmpty { AnywhereType.Category.DEFAULT_CATEGORY }
                                    AnywhereApplication.sRepository.insertPage(
                                            PageEntity.Builder().apply {
                                                title = category
                                                priority = AnywhereApplication.sRepository.allPageEntities.value?.size ?: 0
                                            }
                                    )
                                    it.category = category
                                }
                                viewModel.insert(it)
                            }
                        }
                        dismiss()
                    }
                }
            }

            override fun onFailure(call: Call<RuleEntity>, t: Throwable) {
                Timber.e(t)
            }
        })
    }
}