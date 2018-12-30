#include "Klient.h"
using namespace std;

pthread_mutex_t Klient::clients_mutex = PTHREAD_MUTEX_INITIALIZER;
std::map<std::string, Klient*>Klient::CLIENTS;

Klient::Klient(std::string nick, std::string login, std::string password) {
	this -> nick = nick;
	this->login = login;
	this->password = password;
	this->logged_in = false;
	this->socket = -1;
	this->description = " ";
	pthread_mutex_lock(&Klient::clients_mutex);
	Klient::CLIENTS[login] = this;
	pthread_mutex_unlock(&Klient::clients_mutex);
}

std::list<std::string> split_string(std::string text, char sep) {
	std::string temp;
	std::list<std::string> list;
	for (size_t i = 0; i < text.length(); i++) {
		if (text[i] != sep)
			temp = temp + text[i];
		else {
			list.push_back(std::string(temp));
			temp.clear();
		}
	}
	list.push_back(std::string(temp));
	return list;
}
