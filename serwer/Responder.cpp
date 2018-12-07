#include "Responder.h"
using namespace std;
Responder::Responder(int socket){
	this->socket = socket;
}

Responder::~Responder(){}

void Responder::readAndRespond(){
	memset(this->buf, 0 , sizeof this->buf);
	while (read(this -> socket, this ->buf, 1000) > 0) {
		printf("Received: %s", this->buf);
		std::string bufo = this->buf;
		memset(this->buf, 0 , sizeof this->buf);
		//if(bufo.at(0)=='4') continue;
		bufo = bufo.substr(0, bufo.length()-1);
		cout << bufo <<  ", dlugosc:" << bufo.length() << endl;

		//cutout code from received message
		if(bufo.length() < 4) continue;
		if(!isdigit(bufo.at(0)) || !isdigit(bufo.at(1)) || !isdigit(bufo.at(2)) ||
			!bufo.at(3) == '|') continue;
		int kod = std::stoi(bufo.substr(0, 3));
		bufo = bufo.substr(4, bufo.length() - 4);

		switch (kod){
		case 100:
			message(bufo);
			break;
		case 101:
			login(bufo);
			break;
		case 102:
			logout(bufo);
			break;
		case 103:
			user_register(bufo);
			break;
		case 104:
			search(bufo);
			break;
		case 105:
			addFriend(bufo);
			break;
		case 106:
			sendFriends(bufo);
			break;
		case 107:
			sendHistory(bufo);
			break;
		case 108:
			newConv(bufo);
			break;
		case 109:
			change_description(bufo);
			break;
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
	if ((msg.length() > 0) && this -> klient && this->klient->logged_in) {
		Message* m = new Message(msg, this->klient);
		Conversation* c = Conversation::ALL_CONVS[ID_conv];
		c->push_message(m);
		for (Klient* k : c->getClients()) {
			if (k->login != this->klient->login) sendMsg(m, k, ID_conv);
		}
		cout << "Wyslano\n";
		send_info_code("400|1");
		return;
	}
	cout << "Nie wyslano";
	send_info_code("400|0");
	return;
}

void Responder::login(string buf){
	std::list<std::string> data = split_string(buf, '|');
	std::string login = data.front(); data.pop_front();
	std::string password = data.front(); data.pop_front();
	cout << "Logowanie: " << login << " " << password<<endl;

	std::map<string,Klient*>::iterator klient = Klient::CLIENTS.find(login);
	if (klient!=Klient::CLIENTS.end() && klient->second->password == password) {
		klient->second->logged_in = true;
		klient->second->socket = this->socket;
		this->klient = klient->second;
		string temp = this->klient->nick + "|" + this->klient->description + "|";
		if(this->klient->friends.size() == 0) temp += " ";
		for(Klient* k : this->klient->friends){
			temp = temp + k->nick+","+k->login+","+k->description+","+k->str_log()+",";
		}
		if(temp.at(temp.length()-1) == ',') temp = temp.substr(0, temp.length() - 1);
		cout << "Udane\n";
		send_info_code("401|"+temp);
		return;
	}
	cout << "Nieudane\n";
	send_info_code("401|0");
	return;
}

void Responder::logout(string buf){
	cout << "Logout: " << this->klient->login << endl;
	if (this -> klient && this->klient->logged_in) {
		this->klient->logged_in = false;
		this->klient->socket = -1;
		this->klient = NULL;
		cout << "Udane\n";
		send_info_code("402|1");
		return;
	}
	else {
		cout << "Nieudane\n";
		send_info_code("402|0");
		return;
	}
}

void Responder::user_register(string buf){
	list<string> data = split_string(buf, '|');
	string nick = data.front(); data.pop_front();
	string login = data.front(); data.pop_front();
	string password = data.front(); data.pop_front();
	
	cout << "Rejestracja: " << nick << " " << login << " " << password << endl;
	bool valid = check_registration_validity(nick, login, password);
	if (valid) {
		cout << "Udane\n";
		Klient* k = new Klient(nick, login, password);
		send_info_code("403|1");
	}
	else{
		cout << "Nieudane\n";
		send_info_code("403|0");
	} 
	return;
}

void Responder::search(string buf){
	cout << this -> klient->login << " szuka " << buf << endl;
	if (this->klient && this->klient->logged_in) {
		string login = buf;
		pthread_mutex_lock(&Klient::clients_mutex);
		std::map<string, Klient*>::iterator klient_it = Klient::CLIENTS.find(login);
		if (klient_it != Klient::CLIENTS.end()) {
			pthread_mutex_unlock(&Klient::clients_mutex);
			cout << "Udane\n";
			send_info_code("404|" + klient_it->second->nick);
			return;
		}
		pthread_mutex_unlock(&Klient::clients_mutex);
	}
	cout << "Nieudane\n";
	send_info_code("404|0");
	return;
}

void Responder::addFriend(string buf){
	cout << this -> klient->login << " dodaje " << buf << endl;
	string login = buf;
	if (this->klient && this->klient->logged_in) {
		pthread_mutex_lock(&Klient::clients_mutex);
		std::map<string, Klient*>::iterator klient_it = Klient::CLIENTS.find(login);
		if (klient_it != Klient::CLIENTS.end()) {
			cout << klient_it->first << " " << login << endl;
			pthread_mutex_unlock(&Klient::clients_mutex);
			this->klient->friends.push_back(klient_it->second);
			cout << "Udane\n";
			send_info_code("405|" + klient_it->second->nick+"|"+klient_it->second->description);
			return;
		}
		pthread_mutex_unlock(&Klient::clients_mutex);
	}
	cout << "Nieudane\n";
	send_info_code("405|0");
	return;
}

void Responder::sendFriends(string buf){
	cout << this->klient->login << " pobiera znajomych " << endl;
	string temp = "";
	if (this->klient && this->klient->logged_in) {
		for (Klient* k : this->klient->friends) {
			temp = temp + k->nick + "," + k->login + ","+k->description+","+k->str_log()+"|";
		}
		if(temp.length() > 0)
			temp = temp.substr(0, temp.length() - 1);
		cout << "Udane\n";
		send_info_code("406|" + temp);
		return;
	}
	cout << "Nieudane\n";
	send_info_code("406|0");
	return;
}

void Responder::sendHistory(string buf) {
	cout << this -> klient->login << " pobiera historie konwersacji o id " << buf << endl;
	int id_conv = stoi(buf);
	map<int, Conversation*>::iterator conv_it = Conversation::ALL_CONVS.find(id_conv);
	if (conv_it != Conversation::ALL_CONVS.end()) {
		string buf = "407";
		Conversation* c = conv_it->second;
		for (Message* m : c->getMessages()) {
			buf = buf + "|" + m->toString2();
		}
		if(buf == "407") buf = buf + "|";
		cout << "Udane\n";
		send_info_code(buf);
		return;
	}
	cout << "Nieudane\n";
	send_info_code("407|0");
	return;
}

void Responder::newConv(string buf) {
	cout << this->klient->login << " tworzy konwersacje" << endl;
	list<string> logins = split_string(buf, '|');
	list<Klient*> clients;
	for (string login : logins)
		clients.push_back(Klient::CLIENTS[login]);
	Conversation* c = new Conversation(clients);
	cout << "Udane\n";
	send_info_code("408|" + to_string(c->getID()));
}

void Responder::change_description(string buf){
	cout << this->klient->login << " zmienia opis na " << buf << endl;
	if(this -> klient && this -> klient -> logged_in){
		if(buf.length() == 0) buf = " ";
		this->klient->description = buf;
		cout << "Udane\n";
		send_info_code("409|1");
	}else{
		cout << "Nieudane\n";
		send_info_code("409|0");
	}
	return;
}



bool Responder::sendMsg(Message*m, Klient* to, int id_conv) {
	string to_send = "500|" + m->toString1(id_conv) + "\n";
	cout << to_send << endl;
	const char* msg = to_send.c_str();
	write(to->socket, msg, to_send.length());
	return true;
}

list<string> Responder::split_string(string text, char sep, bool msg) {
	string temp = "";
	list<string> list;
	for (size_t i = 0; i < text.length(); i++) {
		if (text[i] != sep || (msg && list.size() == 1))
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
	cout << code << endl;
	code += "\n";
	const char* c = code.c_str();
	write(this -> socket, c, code.length());
	return;
}

bool Responder::check_registration_validity(string nick, string login, string password)
{
	char separators[] = { ',', '.', '|', '\\', '\'', '\"'};
	bool n = nick.length() > 1 && !contains(nick, separators);
	bool l = login.length() > 1 && !contains(login, separators);
	bool p = password.length() > 1 && !contains(password, separators);
	pthread_mutex_lock(&Klient::clients_mutex);
	std::map<string, Klient*>::iterator klient_it = Klient::CLIENTS.find(login);
	bool nz = (klient_it == Klient::CLIENTS.end());
	pthread_mutex_unlock(&Klient::clients_mutex);
	return n && l && p && nz;
}

bool Responder::contains(string text, char* chars) {
	for (int i = 0; i < sizeof(chars)/sizeof(char); i++) {
		for (int j = 0; j < text.length(); j++) {
			if (text[j] == chars[i]) return true;
		}
	}
	return false;
}