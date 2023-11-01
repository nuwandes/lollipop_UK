package org.nghru_uk.ghru.ui.hlqself.languagelist

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.QuestionnaireListFragmentBinding
import org.nghru_uk.ghru.databinding.QuestionnaireSelfListFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.util.autoCleared
import timber.log.Timber
import javax.inject.Inject


class QuestionnaireListFragment : Fragment(), Injectable, SwipeRefreshLayout.OnRefreshListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<QuestionnaireSelfListFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: QuestionnaireListViewModel

    private var adapter by autoCleared<QuestionnaireListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<QuestionnaireSelfListFragmentBinding>(
            inflater,
            R.layout.questionnaire_self_list_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel;

        val adapter = QuestionnaireListAdapter(dataBindingComponent, appExecutors) { questionnaire ->
            Timber.e(questionnaire.toString())
            findNavController().navigate(R.id.action_QuestionnaireListFragment_to_ScanBarcodeFragment , bundleOf("Questionnaire" to questionnaire))
        }

        this.adapter = adapter
        binding.nghruList.adapter = adapter
        binding.nghruList.setLayoutManager(GridLayoutManager(activity, 1))
        viewModel.getQuestionnaire(network =  isNetworkAvailable(), language = "en")
        viewModel.language?.observe(this, Observer { resource ->
            binding.swiperefresh.isRefreshing = false
            if (resource?.data != null) {
                adapter.submitList(resource.data)
                binding.executePendingBindings()
            } else {
                adapter.submitList(emptyList())
                binding.executePendingBindings()
            }
        })
        binding.swiperefresh.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        viewModel.retry()
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }
    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}