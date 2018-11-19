#include "pch.h"
#include "Klient.h"


Klient::Klient(std::string nick, std::string login, std::string password,
		bool logged_in = false, int socket = -1) {
	this -> nick = nick;
	this->login = login;
	this->password = password;
	this->logged_in = logged_in;
	this->socket = socket;
}

Klient::Klient() {}

Klient::~Klient()
{
}
