package com.road801.android.view.main.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.road801.android.R
import com.road801.android.databinding.ActivityHomeBinding
import com.road801.android.view.main.me.MeFragment
import com.road801.android.view.main.point.PointFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding


    private val homeFragment = HomeFragment()
    private val pointFragment = PointFragment()
    private val meFragment = MeFragment()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

//        binding.homeBottomNavigation.setOnItemSelectedListener {
//            when (it.itemId) {
//                MENU_HOME -> {
//                    Toast.makeText(this, "bottom_menu_home", Toast.LENGTH_SHORT).show()
//                    return@setOnItemSelectedListener true
//                }
//                MENU_POINT -> {
//                    Toast.makeText(this, "bottom_menu_point", Toast.LENGTH_SHORT).show()
//                    return@setOnItemSelectedListener true
//                }
//                MENU_MY_INFO -> {
//                    Toast.makeText(this, "bottom_menu_my_info", Toast.LENGTH_SHORT).show()
//                    return@setOnItemSelectedListener true
//                }
//            }
//            return@setOnItemSelectedListener false
//        }


        bottomNavigationRounded()
        setupNavController()
        handleBottomNavigationUI()
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.homeBottomNavigation.setupWithNavController(navController)
        
    }

    private fun bottomNavigationRounded() {
        val radius = resources.getDimension(R.dimen._12dp)
        val bottomNavigationViewBackground =
            binding.homeBottomNavigation.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                .build()
    }


    private fun handleBottomNavigationUI() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_home) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val navGraph = destination.parent
            when (navGraph?.id) {
                R.id.navi_main -> {
                    if (destination.id == R.id.home_fragment
                        || destination.id == R.id.point_fragment
                        || destination.id == R.id.me_fragment
                    ) {
                        binding.homeBottomNavigation.visibility = View.VISIBLE
                    } else {
                        binding.homeBottomNavigation.visibility = View.GONE
                    }
                }
                else -> {
                    binding.homeBottomNavigation.visibility = View.GONE
                }
            }
        }
    }

}