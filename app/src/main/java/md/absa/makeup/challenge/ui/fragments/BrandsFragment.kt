package md.absa.makeup.challenge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import md.absa.makeup.challenge.R
import md.absa.makeup.challenge.common.Constants
import md.absa.makeup.challenge.common.Utils
import md.absa.makeup.challenge.data.api.resource.Status
import md.absa.makeup.challenge.databinding.FragmentBrandsBinding
import md.absa.makeup.challenge.ui.adapters.BrandsAdapter
import md.absa.makeup.challenge.ui.viewmodels.MakeUpViewModel
import timber.log.Timber

@AndroidEntryPoint
class BrandsFragment : Fragment() {

    private val viewModel by viewModels<MakeUpViewModel>()
    private var _binding: FragmentBrandsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Utils.setStatusBarColor(requireActivity().window, resources.getColor(R.color.purple_500, resources.newTheme()), true)
        _binding = FragmentBrandsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Brands"

        binding.fetchMakeup.setOnClickListener {
            viewModel.fetchMakeup()
        }
        viewModel.makeUpItems.observe(viewLifecycleOwner) { response ->
            Timber.e("brands response", response.toString())

            when (response.status) {
                Status.LOADING -> {
                    Timber.e("brands LOADING", response.toString())
                    binding.fetchMakeup.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.noData.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    Timber.e("brands SUCCESS", response.toString())

                    binding.fetchMakeup.visibility = View.GONE
                    binding.noData.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    response.data?.let { data ->
                        Snackbar.make(
                            binding.root,
                            response.message.toString(),
                            LENGTH_LONG
                        ).show()
                        Timber.e("brands success", data.toString())
                        observeDBForBrands()
                    }
                }
                Status.ERROR -> {
                    Timber.e("brands ERROR", response.toString())

                    binding.fetchMakeup.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.noData.visibility = View.VISIBLE
                    Snackbar.make(
                        binding.root,
                        response.message.toString(),
                        LENGTH_LONG
                    ).show()
                    Timber.e(response.toString())
                }
            }
        }

        /**
         * Observe data directly via Flow as it is inserted into Room
         */
        observeDBForBrands()
    }

    private fun observeDBForBrands() {
        viewModel.getBrands()
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach {
                if (it.isNotEmpty()) {
                    Timber.tag("BRANDS...").e(it.toString())
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.noData.visibility = View.GONE
                    binding.fetchMakeup.visibility = View.GONE
                    setupRecyclerView(it)
                } else {
                    Snackbar.make(
                        binding.root,
                        "No brands found",
                        LENGTH_LONG
                    ).show()
                    binding.recyclerView.visibility = View.GONE
                    binding.noData.visibility = View.VISIBLE
                    binding.fetchMakeup.visibility = View.VISIBLE
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setupRecyclerView(brands: List<String>) {
        val adapter = BrandsAdapter(brands)
        binding.recyclerView.layoutManager = GridLayoutManager(requireActivity(), Constants.BRANDS_GRID_LAYOUT_COLUMNS)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
