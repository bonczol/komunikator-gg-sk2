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
	cout << "dodawanie" << endl;
	pthread_mutex_lock(&Klient::clients_mutex);
	Klient::CLIENTS[login] = this;
	pthread_mutex_unlock(&Klient::clients_mutex);
	cout << "dodano" << endl;
}


//initialize registered clients at the start
/*static void initialize_clients() {
	Klient::CLIENTS.clear();
	std::ifstream inFile;
	inFile.open("klienci.txt");
	if (!inFile) {
		std::cout << "Unable to open file datafile.txt";
		exit(1);   // call system to stop
	}
	std::string line;
	std::list<std::string> nicks;
	std::list<std::string> logins;
	std::list<std::string> passwords;
	std::list<std::pair<Klient*, std::list<std::string>> > friends_names_list;
	while (inFile >> line) {
		std::list<std::string> data = split_string(line, '|');
		std::string nick = data.front(); data.pop_front();
		std::string login = data.front(); data.pop_front();
		std::string password = data.front(); data.pop_front();
		Klient* k = new Klient(nick, login, password, false);
		Klient::CLIENTS[k->login] = k;
		friends_names_list.push_back(std::pair<Klient*, std::list<std::string>>
			(k, split_string(data.front(), ',')));
	}
	for (const auto& klient_friends_names : friends_names_list) {
		Klient* k = klient_friends_names.first;
		for (const auto& friend_name : klient_friends_names.second) {
			k->friends.push_back(Klient::CLIENTS[friend_name]);
		}
	}
	return;
}*/

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
