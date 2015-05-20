package com.algd.oauth

import com.algd.oauth.data.DataManager
import com.algd.oauth.data.model.{AuthorizationData, Client, User}
import com.algd.oauth.utils.OAuthParams

import scala.concurrent.Future

case class TestUser(id: String, scope: Set[String]) extends User

class MyDataManager extends DataManager[TestUser] {
  val clients: scala.collection.mutable.Map[String, (String, Client)] = scala.collection.mutable.Map()
  val users: scala.collection.mutable.Map[String, (String, TestUser)] = scala.collection.mutable.Map()
  val authCodes: scala.collection.mutable.Map[String, AuthorizationData[TestUser]] = scala.collection.mutable.Map()
  val tokenDatas: scala.collection.mutable.Map[String, AuthorizationData[TestUser]] = scala.collection.mutable.Map()
  val refTokenDatas: scala.collection.mutable.Map[String, AuthorizationData[TestUser]] = scala.collection.mutable.Map()

  ///////////////////////////////////////////////

  def getClient(id: String)
      (implicit params: OAuthParams): Future[Option[Client]] = {
    Future.successful(clients.get(id).map(_._2))
  }

  def getClient(id: String, secret: String)
      (implicit params: OAuthParams): Future[Option[Client]] = {
    Future.successful(clients.get(id).find(_._1 == secret).map(_._2))
  }

  def getUser(username: String, password: String)
      (implicit params: OAuthParams): Future[Option[TestUser]] = {
    Future.successful(users.get(username).find(_._1 == password).map(_._2))
  }

  def getAuthCodeData(code: String)
      (implicit params: OAuthParams): Future[Option[AuthorizationData[TestUser]]] = {
    Future.successful(authCodes.get(code))
  }

  def removeAuthCodeData(code: String)
      (implicit params: OAuthParams): Future[Option[String]] = {
    Future.successful(authCodes.remove(code).map(_ => code))
  }

  def getRefreshTokenData(refreshToken: String)
      (implicit params: OAuthParams): Future[Option[AuthorizationData[TestUser]]] = {
    Future.successful(refTokenDatas.get(refreshToken))
  }

  def removeRefreshTokenData(refreshToken: String)
      (implicit params: OAuthParams): Future[Option[String]] = {
    Future.successful(refTokenDatas.remove(refreshToken).map(_ => refreshToken))
  }

  def getAccessTokenData(token: String)
      (implicit params: OAuthParams) : Future[Option[AuthorizationData[TestUser]]] = {
    Future.successful(tokenDatas.get(token))
  }

  def removeAccessTokenData(token: String)
      (implicit params: OAuthParams): Future[Option[String]] = {
    Future.successful(tokenDatas.remove(token).map(_ => token))
  }

  def isValidRedirectUri(uri: String, clientUris: List[String])
      (implicit params: OAuthParams): Boolean = {
    clientUris.exists(clientUri => uri.startsWith(clientUri))
  }

  def generateAccessToken(authInfo: AuthorizationData[TestUser])
      (implicit params: OAuthParams) : Future[String] = {
    val token = java.util.UUID.randomUUID().toString
    tokenDatas += token -> AuthorizationData(authInfo.client, authInfo.user, authInfo.scope)
    Future.successful(token)
  }

  def generateRefreshToken(authInfo: AuthorizationData[TestUser])
      (implicit params: OAuthParams) : Future[String] = {
    val token = java.util.UUID.randomUUID().toString
    refTokenDatas += token -> AuthorizationData(authInfo.client, authInfo.user, authInfo.scope)
    Future.successful(token)
  }

  def generateAuthCode(authInfo: AuthorizationData[TestUser])
      (implicit params: OAuthParams) : Future[String] = {
    val code = java.util.UUID.randomUUID.toString.substring(0, 4).toUpperCase
    authCodes += code -> authInfo
    Future.successful(code)
  }
}