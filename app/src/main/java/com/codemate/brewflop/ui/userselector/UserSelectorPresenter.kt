package com.codemate.brewflop.ui.userselector

import android.content.Context
import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.DayCountUpdater
import com.codemate.brewflop.R
import com.codemate.brewflop.data.local.BrewFailureLogger
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.model.User
import com.codemate.brewflop.data.network.model.UserListResponse
import com.codemate.brewflop.ui.base.BasePresenter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSelectorPresenter(
        private val slackApi: SlackApi,
        private val brewFailureLogger: BrewFailureLogger,
        private val dayCountUpdater: DayCountUpdater,
        private val messageCreator: MessageCreator) : BasePresenter<UserSelectorView>() {
    fun loadUsers() {
        super.ensureViewIsAttached()
        getView()?.showProgress()

        slackApi.getUsers(BuildConfig.SLACK_AUTH_TOKEN).enqueue(object : Callback<UserListResponse> {
            override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {
                if (response.isSuccessful) {
                    val users = response.body().members
                            .toMutableList()
                            .filter { !it.isBot
                                        && !it.profile.firstName.startsWith("Ext-")
                                        && it.realName != "slackbot"
                            }
                            .sortedBy { it.profile.realNameNormalized }

                    getView()?.showSearchResults(users)
                    getView()?.hideProgress()
                } else {
                    getView()?.showError()
                }
            }

            override fun onFailure(call: Call<UserListResponse>, t: Throwable) {
                getView()?.showError()
            }
        })
    }

    fun postMessageToSlack(user: User) {
        super.ensureViewIsAttached()

        brewFailureLogger.incrementFailureCountForUser(user)
        messageCreator.createMessage(user, brewFailureLogger, dayCountUpdater.dayCount) { message ->
            slackApi.postMessage(BuildConfig.SLACK_AUTH_TOKEN, "iiro-test", message).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        getView()?.messagePostedSuccessfully()
                    } else {
                        getView()?.errorPostingMessage()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    getView()?.errorPostingMessage()
                }
            })
        }
    }
}