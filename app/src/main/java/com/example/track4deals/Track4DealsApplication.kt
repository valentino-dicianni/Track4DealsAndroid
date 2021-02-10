package com.example.track4deals

import android.app.Application
import com.example.track4deals.data.repository.AuthRepository
import com.example.track4deals.data.repository.ProductRepository
import com.example.track4deals.data.database.ProductDB
import com.example.track4deals.data.repository.UserRepository
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.services.AuthService
import com.example.track4deals.services.utils.ConnectivityInterceptor
import com.example.track4deals.services.ProductDataService
import com.example.track4deals.services.OffersService
import com.example.track4deals.services.ProfileService
import com.example.track4deals.services.utils.JWTinterceptor
import com.example.track4deals.services.UserDataService
import com.example.track4deals.ui.login.LoginViewModelFactory
import com.example.track4deals.ui.offers.OffersViewModelFactory
import com.example.track4deals.ui.profile.ChangePasswordDialogFragment
import com.example.track4deals.ui.profile.PasswordConfirmationDialogFragment
import com.example.track4deals.ui.profile.ProfileViewModelFactory
import com.example.track4deals.ui.register.RegisterViewModelFactory
import com.example.track4deals.ui.settings.SettingsViewModelFactory
import com.example.track4deals.ui.tracking.TrackingViewModelFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*

class Track4DealsApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@Track4DealsApplication)) // for application context
        bind() from singleton { UserProvider() }
        bind() from singleton { ProductDB(instance()) }
        bind() from singleton { instance<ProductDB>().productDAO() }
        bind() from singleton { ConnectivityInterceptor(instance()) }
        bind() from singleton { JWTinterceptor(instance()) }
        bind() from singleton { OffersService(instance(), instance()) }
        bind() from singleton { ProfileService(instance(), instance()) }
        bind() from singleton { AuthService(instance(), instance()) }
        bind() from singleton { ProductDataService(instance()) }
        bind() from singleton { UserDataService(instance()) }
        bind() from singleton { ProductRepository(instance(), instance()) }
        bind() from singleton { AuthRepository(instance(), instance(), instance()) }
        bind() from singleton { UserRepository(instance()) }

        bind() from provider { RegisterViewModelFactory(instance()) }
        bind() from provider { LoginViewModelFactory(instance()) }
        bind() from provider { OffersViewModelFactory(instance()) }
        bind() from provider { ProfileViewModelFactory(instance()) }
        bind() from provider { TrackingViewModelFactory(instance()) }
        bind() from provider { SettingsViewModelFactory(instance(), instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

}

