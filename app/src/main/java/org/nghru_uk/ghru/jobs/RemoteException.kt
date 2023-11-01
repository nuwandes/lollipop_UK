package org.nghru_uk.ghru.jobs

import retrofit2.Response

class RemoteException(val response: Response<*>) : Exception()
