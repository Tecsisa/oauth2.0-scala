package com.algd.oauth.granter

import com.algd.oauth.data.model.{AuthorizationData, TokenResponse, Client}
import com.algd.oauth.exception.OAuthError._
import com.algd.oauth.utils.OAuthParams._
import org.joda.time.DateTime

class RefreshTokenGranterSpec extends GranterSuite {
  dataManager.clients +=
    "rtclient" -> ("client_secret", Client("Test Client", "rtclient", Set("test", "test3"), Set(GrantType.REFRESH_TOKEN), List("http://redirect.com")))
  dataManager.clients +=
    "acclient" -> ("client_secret", Client("Test Client", "acclient", Set("test"), Set(GrantType.AUTHORIZATION_CODE), List("http://redirect.com")))

  val rtGranter = granterFor(new RefreshTokenGranter)

  expect[TokenResponse] (
    "A client should be able to obtain an access token from a valid refresh token") {
    dataManager.refTokenDatas += ("refreshtoken1" -> AuthorizationData(
      dataManager.clients("rtclient")._2,
      Some(dataManager.users("marissa")._2),
      Some(Set("test"))))
    rtGranter(Map(CLIENT_ID -> "rtclient",
      CLIENT_SECRET -> "client_secret",
      GRANT_TYPE -> GrantType.REFRESH_TOKEN,
      REFRESH_TOKEN -> "refreshtoken1"))
  }

  expectError(INVALID_GRANT) (
    "A client shouldn't be able to obtain an access token from a invalid refresh token") {
    rtGranter(Map(CLIENT_ID -> "rtclient",
      CLIENT_SECRET -> "client_secret",
      GRANT_TYPE -> GrantType.REFRESH_TOKEN,
      REFRESH_TOKEN -> "refreshtoken2"))
  }

  expectError(INVALID_GRANT) (
    "A client shouldn't be able to obtain an access token from a valid refresh token that belongs to another client") {
    dataManager.refTokenDatas += ("refreshtoken3" -> AuthorizationData(
      dataManager.clients("acclient")._2,
      Some(dataManager.users("marissa")._2),
      Some(Set("test"))))
    rtGranter(Map(CLIENT_ID -> "rtclient",
      CLIENT_SECRET -> "client_secret",
      GRANT_TYPE -> GrantType.REFRESH_TOKEN,
      REFRESH_TOKEN -> "refreshtoken3"))
  }

  expectError(INVALID_GRANT) (
    "A client shouldn't be able to obtain an access token from an expired refresh token") {
    dataManager.refTokenDatas += ("refreshtoken4" -> AuthorizationData(
      dataManager.clients("rtclient")._2,
      Some(dataManager.users("marissa")._2),
      Some(Set("test")),
      creationDate = DateTime.now.minusSeconds(40000)))
    rtGranter(Map(CLIENT_ID -> "rtclient",
      CLIENT_SECRET -> "client_secret",
      GRANT_TYPE -> GrantType.REFRESH_TOKEN,
      REFRESH_TOKEN -> "refreshtoken4"))
  }

  expectError(INVALID_REQUEST) (
    "A client shouldn't be able to obtain an access token without refresh token") {
    rtGranter(Map(CLIENT_ID -> "rtclient",
      CLIENT_SECRET -> "client_secret",
      GRANT_TYPE -> GrantType.REFRESH_TOKEN))
  }

  /*expectError(INVALID_SCOPE) ( //TODO: different? more?
    "A client shouldn't be able to obtain an access token with a refresh token requesting different scope") {
    dataManager.refTokenDatas += ("refreshtoken5" -> AuthorizationData(
      dataManager.clients("rtclient")._2,
      dataManager.users("marissa")._2,
      Some(Set("test"))))
    rtGranter(Map(CLIENT_ID -> "rtclient",
      CLIENT_SECRET -> "client_secret",
      GRANT_TYPE -> GrantType.REFRESH_TOKEN,
      REFRESH_TOKEN -> "refreshtoken5",
      SCOPE -> "test3"))
  }*/

}
