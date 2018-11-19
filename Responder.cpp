#include "pch.h"
#include "Responder.h"
using namespace std;
Responder::Responder(int socket, list<Klient*>* clients){
	this->socket = socket;
	this->clients = clients;
}

Responder::~Responder(){}

void Responder::readAndRespond(){
	while (read(this -> socket, this ->buf, 1000) > 0) {
		string buf = string(this->buf);
		printf("Dlugosc: %ld\n", buf.length);

		//cutout code from received message
		int kod = std::stoi(buf.substr(0, 3));
		buf = buf.substr(4, buf.length - 4);

		switch (kod){
		case 100:
			message(this->buf);
		case 101:
			login(this->buf);
		case 102:
			logout(this->buf);
		case 103:
			user_register(this->buf);
		case 104:
			search(this->buf);
		case 105:
			addFriend(this->buf);
		case 106:
			sendFriends(this->buf);
		default:
			break;
		}

	}
}


//send message to addressed clients
void Responder::message(string buf){
	list<string> data = split_string(buf, '|', true);
	list<string> logins = split_string(data.front, ',');
	data.pop_front();
	string msg = data.front();
	if (buf.length > 1 && this->klient->logged_in) {
		for (string log : logins) {
			std::cout << log;
			for (Klient* k : this->klient->friends) {
				if(log == k->login) write(k->socket, msg, 1000);
			}
		}
	}
}

void Responder::login(string buf){
	std::list<std::string> data = split_string(buf, '|');
	std::string login = data.front(); data.pop_front();
	std::string password = data.front(); data.pop_front();

	//TODO: mutex na clients
	for (Klient* klient : *clients) {
		std::cout << klient->login;
		if (klient->login == login && klient->password == password) {
			klient->logged_in = true;
			klient->socket = this ->socket;
			this->klient = klient;
			send_info_code("401|1");
			//send_friends_list(klient, csd);
			return;
		}
		send_info_code("401|0");
		return;
	}
}

void Responder::logout(string buf){
	if (this->klient->logged_in) {
		std::cout << this->klient->login;
		this->klient->logged_in = false;
		this->klient->socket = NULL;
		this->klient = NULL;
		send_info_code("402|1");
		return;
	}
	else {
		send_info_code("402|0");
		return;
	}
}

void Responder::user_register(string buf){
	list<string> data = split_string(this->buf, '|');
	string nick = data.front(); data.pop_front();
	string login = data.front(); data.pop_front();
	string password = data.front(); data.pop_front();
	
	
	bool valid = check_registration_validity(nick, login, password);
	if (valid) {
		Klient* k = new Klient(nick, login, password);
		clients ->push_back(k);
		send_info_code("403|1");	//powodzenie
	}
	else send_info_code("403|0");	//niepowodzenie
	return;
}

void Responder::search(string buf){
	if (this->klient && this->klient->logged_in) {
		string login = buf;
		for (Klient* k : *clients) {
			if (k->login == buf) {
				send_info_code("404|" + k->nick);
				return;
			}
		}
	}
	send_info_code("404|0");
	return;
}

void Responder::addFriend(string buf){
	string login = buf;
	if (this->klient && this->klient->logged_in) {
		for (Klient* k : *clients) {
			if (k->login == login) {
				this->klient->friends.push_back(k);
				send_info_code("405|1");
				return;
			}
		}
	}
	send_info_code("405|0");
	return;
}

void Responder::sendFriends(string buf){
	string temp;
	if (this->klient && this->klient->logged_in) {
		for (Klient* k : this->klient->friends) {
			temp = temp + klient->nick + "," + klient->login + "|";
		}
		temp = temp.substr(0, temp.length - 1);
		send_info_code("406|" + temp);
		return;
	}
	send_info_code("406|0");
	return;
}



list<string> Responder::split_string(string text, char sep, bool msg = false) {
	string temp;
	list<string> list;
	for (size_t i = 0; i < text.length; i++) {
		if (text[i] != sep || (msg && list.size == 1))
			temp = temp + text[i];
		else {
			list.push_back(string(temp));
			temp.clear();
		}
	}
	list.push_back(string(temp));
	return list;
}

void Responder::send_info_code(string code) {
	write(this -> socket, code, 1000);
	return;
}

bool Responder::check_registration_validity(string nick, string login, string password)
{
	char separators[] = { ',', '.', '|', '\\', '\'', '\"'};
	bool n = nick.length > 1 && !contains(nick, separators);
	bool l = login.length > 1 && !contains(login, separators);
	bool p = password.length > 1 && !contains(password, separators);
	return n && l && p;
}

bool Responder::contains(string text, char* chars) {
	for (int i = 0; i < sizeof(chars)/sizeof(char); i++) {
		for (int j = 0; j < text.length; j++) {
			if (text[j] == chars[i]) return true;
		}
	}
	return false;
}