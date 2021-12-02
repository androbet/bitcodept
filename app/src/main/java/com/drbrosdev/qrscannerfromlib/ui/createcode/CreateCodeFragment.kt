package com.drbrosdev.qrscannerfromlib.ui.createcode

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCreateCodeBinding
import com.drbrosdev.qrscannerfromlib.util.collectFlow
import com.drbrosdev.qrscannerfromlib.util.createLoadingDialog
import com.drbrosdev.qrscannerfromlib.util.getColor
import com.drbrosdev.qrscannerfromlib.util.hideKeyboard
import com.drbrosdev.qrscannerfromlib.util.showSnackbarShort
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateCodeFragment: Fragment(R.layout.fragment_create_code) {
    private val binding by viewBinding(FragmentCreateCodeBinding::bind)
    private val viewModel: CreateCodeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        val loadingDialog = createLoadingDialog()
        //val requester = CreateQRCodeRequest()

        collectFlow(viewModel.events) {
            when(it) {
                is CreateCodeEvents.ShowCodeSaved -> {
                    loadingDialog.hide()
                    findNavController().navigateUp()
                }
                is CreateCodeEvents.ShowLoading -> {
                    loadingDialog.show()
                }
                is CreateCodeEvents.ShowError -> {
                    loadingDialog.hide()
                    showSnackbarShort(
                        message = "Creation failed. Check internet connection.",
                        anchor = binding.buttonCreateCode
                    )
                }
                CreateCodeEvents.CodeTextIsEmpty -> {
                    showSnackbarShort(
                        message = "Text field is empty.",
                        anchor = binding.buttonCreateCode
                    )
                }
            }
        }

        binding.apply {
            imageViewBack.setOnClickListener {
                hideKeyboard()
                findNavController().navigateUp()
            }

            buttonCreateCode.setOnClickListener {
                viewModel.createCode(
                    codeContent = editTextCodeContent.text.toString().trim(),
                    colorInt = getColor(R.color.candy_teal)
                )
            }
        }
    }
}