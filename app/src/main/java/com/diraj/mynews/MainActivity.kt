package com.diraj.mynews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.diraj.mynews.ui.IActionBarInterface
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, IActionBarInterface {

    private lateinit var navigationController: NavController

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationController = findNavController(R.id.navigationHostFragment)
    }

    override fun onSupportNavigateUp() = navigationController.navigateUp()

    override fun setUpActionBar() {
        Timber.d("setUpActionBar")
    }

    override fun setUpToolbar(collapsingToolbarLayout: CollapsingToolbarLayout, toolbar: Toolbar) {
        Timber.d("setUpToolbar")
        setSupportActionBar(toolbar)

    }
}
