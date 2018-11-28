#include "pch.h"
#include "Responder.h"
using namespace std;
Responder::Responder(int socket){
	this->socket = socket;
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
			message(buf);
		case 101:
			login(buf);
		case 102:
			logout(buf);
		case 103:
			user_register(buf);
		case 104:
			search(buf);
		case 105:
			addFriend(buf);
		case 106:
			sendFriends(buf);
		case 107:
			sendHistory(buf);
		case 108:
			newConv(buf);
		default:
			break;
		}

	}
}


//send message to addressed clients
void Responder::message(string buf){
	list<string> data = split_string(buf, '|', true);
	int ID_conv = stoi(data.front()); data.pop_front();
	string msg = data.front();
	if (msg.length > 1 && this->klient->logged_in) {
		Message* m = new Message(msg, this->klient);
		Conversation* c = Conversation::ALL_CONVS[ID_conv];
		c->push_message(m);
		for (Klient* k : c->getClients()) {
			if (k->login != this->login) sendMsg(m, k, ID_conv);
		}
		send_info_code("400|1");
		return;
	}
	send_info_code("400|0");
	return;
}

void Responder::login(string buf){
	std::list<std::string> data = split_string(buf, '|');
	std::string login = data.front(); data.pop_front();
	std::string password = data.front(); data.pop_front();

	std::map<string,Klient*>::iterator klient = Klient::CLIENTS.find(login);
	if (klient!=Klient::CLIENTS.end() && klient->second->password == password) {
		klient->second->logged_in = true;
		klient->second->socket = this->socket;
		this->klient = klient->second;
		send_info_code("401|1");
		return;
	}
	send_info_code("401|0");
	return;
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
		send_info_code("403|1");	//powodzenie
	}
	else send_info_code("403|0");	//niepowodzenie
	return;
}

void Responder::search(string buf){
	if (this->klient && this->klient->logged_in) {
		string login = buf;
		pthread_mutex_lock(Klient::clients_mutex);
		std::map<string, Klient*>::iterator klient_it = Klient::CLIENTS.find(login);
		if (klient_it != Klient::CLIENTS.end()) {
			pthread_mutex_unlock(Klient::clients_mutex);
			send_info_code("404|" + klient_it->second->nick);
			return;
		}
		pthread_mutex_unlock(Klient::clients_mutex);
	}
	send_info_code("404|0");
	return;
}

void Responder::addFriend(string buf){
	string login = buf;
	if (this->klient && this->klient->logged_in) {
		pthread_mutex_lock(Klient::clients_mutex);
		std::map<string, Klient*>::iterator klient_it = Klient::CLIENTS.find(login);
		if (klient_it != Klient::CLIENTS.end()) {
			pthread_mutex_unlock(Klient::clients_mutex);
			this->klient->friends.push_back(klient_it->second);
			send_info_code("405|1");
			return;
		}
		pthread_mutex_unlock(Klient::clients_mutex);
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

void Responder::sendHistory(string buf) {
	int id_conv = stoi(buf);
	map<int, Conversation*>::iterator conv_it = Conversation::ALL_CONVS.find(id_conv);
	if (conv_it != Conversation::ALL_CONVS.end()) {
		string buf = "407";
		Conversation* c = conv_it->second;
		for (Message* m : c->getMessages()) {
			buf = buf + "|" + m->toString2();
		}
		send_info_code(buf);
		return;
	}
	send_info_code("407|0");
	return;
}

void Responder::newConv(string buf) {
	list<string> logins = split_string(buf, '|');
	list<Klient*> clients;
	for (string login : logins)
		clients.push_back(Klient::CLIENTS[login]);
	Conversation* c = new Conversation(clients);
	send_info_code("408|" + to_string(c->getID));
}



bool Responder::sendMsg(Message*m,  Klient* to, int id_conv) {
	const char* msg = ("500|" + m->toString1(id_conv)).c_str();
	write(to->socket, msg, 1000);
	return true;
}

list<string> Responder::split_string(string text, char sep, bool msg = false) {
	string temp;
	list<string> list;
	for (size_t i = 0; i < text.length; i++) {
		if (text[i] != sep || (msg && list.size == 2))
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
	const char* c = code.c_str();
	write(this -> socket, c, 1000);
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