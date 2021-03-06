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
		case 110:
			delete_friend(bufo);
			break;
		case 111:
			send_conv_id(bufo);
			break;
		default:
			break;
		}

	}
	if(this -> klient) this->klient->logged_in = false;
}


//send message to addressed clients
void Responder::message(string buf){
	list<string> data = split_string(buf, '|', true);
	int ID_conv = stoi(data.front()); data.pop_front();
	string msg = data.front();
	if ((msg.length() > 0) && this -> klient && this->klient->logged_in) {
		Message* m = new Message(msg, this->klient, ID_conv);
		Conversation* c = Conversation::ALL_CONVS[ID_conv];
		c->push_message(m);
		for (Klient* k : c->getClients()) {
			if (k->login != this->klient->login){
				if(k->logged_in) sendMsg(m, k);
			} 
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
		//check_for_unsent_msgs();
		for(int id_conv : this->klient->ID_convs){
			sendHistory(to_string(id_conv));
		}
		tell_his_friends();
		return;
	}
	cout << "Nieudane\n";
	send_info_code("401|0");
	return;
}

void Responder::logout(string buf){
	if (this -> klient && this->klient->logged_in) {
		cout << "Logout: " << this->klient->login << endl;
		send_info_code("402|1");
		this->klient->logged_in = false;
		tell_his_friends();
		this->klient->socket = -1;
		this->klient = NULL;
		cout << "Udane\n";
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
	if (this->klient && this->klient->logged_in) {
		cout << this -> klient->login << " szuka " << buf << endl;
		bool found = false;
		int n = buf.length();
		string temp = "404|";
		pthread_mutex_lock(&Klient::clients_mutex);
		for(auto const& log_k : Klient::CLIENTS) {
			cout << buf << " " << log_k.first << " " << log_k.first.substr(0, n) << endl;
			if(log_k.first.substr(0, n) == buf) {
				temp += log_k.second->nick + "," + log_k.second->login + ",";
				found = true;
			}
		}
		if(found) temp = temp.substr(0, temp.length() - 1);
		else temp += " ";
		cout << "Udane\n";
		send_info_code(temp);
		pthread_mutex_unlock(&Klient::clients_mutex);
		return;
	}
	cout << "Nieudane\n";
	send_info_code("404|0");
	return;
}

void Responder::addFriend(string buf){
	string login = buf;
	bool already_friend = false;
	if (this->klient && this->klient->logged_in && this->klient->login != buf) {
		cout << this -> klient->login << " dodaje " << buf << endl;
		//check if he is already a friend
		for(Klient* f : this->klient->friends){
			if(f->login == buf) already_friend = true;
		}

		pthread_mutex_lock(&Klient::clients_mutex);
		std::map<string, Klient*>::iterator klient_it = Klient::CLIENTS.find(login);
		if (klient_it != Klient::CLIENTS.end() && !already_friend) {
			//if such client exists
			pthread_mutex_unlock(&Klient::clients_mutex);
			//add each other to friends
			this->klient->friends.push_back(klient_it->second);
			klient_it->second->friends.push_back(this->klient);
			cout << "Udane\n";
			//send info to my client
			send_info_code("405|" + klient_it->second->nick+"|" +klient_it->second->login+"|"+klient_it->second->description +
			 "|" + klient_it->second->str_log());
			//if new friend is logged in, send him info too
			if(klient_it->second->logged_in){
				string info = "405|"+this->klient->nick+"|"+this->klient->login+"|"+
				this->klient->description + "|" + this->klient->str_log()+"\n";
				const char* msg = info.c_str();
				if(klient_it->second->logged_in)
					write(klient_it->second->socket, msg, info.length());
			}
			return;
		}
		pthread_mutex_unlock(&Klient::clients_mutex);
	}
	cout << "Nieudane\n";
	send_info_code("405|0");
	return;
}

void Responder::sendFriends(string buf){
	string temp = "";
	if (this->klient && this->klient->logged_in) {
		cout << this->klient->login << " pobiera znajomych " << endl;
		for (Klient* k : this->klient->friends) {
			temp = temp + k->nick + "," + k->login + ","+k->description+","+k->str_log()+",";
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
		Conversation* c = conv_it->second;
		string code = "407|" + buf+"|";
		for(Klient* k : c->getClients())
			code += k->login + ",";
		code = code.substr(0, code.length() - 1);
		bool empty_conv = true;
		for (Message* m : c->getMessages()) {
			code = code + "|" + m->toString2();
			empty_conv = false;
		}	
		if(empty_conv) code = code + "| ";
		cout << "Udane\n";
		send_info_code(code);
		return;
	}
	cout << "Nieudane\n";
	send_info_code("407|0");
	return;
}

void Responder::newConv(string buf) {
	cout << this->klient->login << " tworzy konwersacje" << endl;
	cout << "buf" << buf << endl;
	list<string> logins = split_string(buf, ',');
	list<Klient*> clients;
	string temp="";
	for(string log : logins){
		temp+=log+",";
	}
	temp = temp.substr(0, temp.length() - 1);
	for (string login : logins)
		clients.push_back(Klient::CLIENTS[login]);
	Conversation* c = new Conversation(clients);
	cout << "Udane\n";
	int id_conv = c->getID();
	send_info_code("408|" + to_string(id_conv)+"|" + temp);

	string info = "502|"+to_string(id_conv)+"|" + temp+ "\n";
	const char* msg = info.c_str();
	for(Klient* k : clients){
		k->ID_convs.push_front(id_conv);
		if(k->logged_in && k!=this->klient)
			write(k -> socket, msg, info.length());
	}
	return;
}

void Responder::change_description(string buf){
	cout << this->klient->login << " zmienia opis na " << buf << endl;
	if(this -> klient && this -> klient -> logged_in && buf != " "){
		if(buf.length() == 0) buf = " ";
		this->klient->description = buf;
		tell_his_friends();
		cout << "Udane\n";
		send_info_code("409|1");
	}else{
		cout << "Nieudane\n";
		send_info_code("409|0");
	}
	return;
}

void Responder::delete_friend(string buf){
	//string login = buf;
	pthread_mutex_lock(&this->klient->friends_mutex);
	for (auto it = this->klient->friends.begin(); it != this->klient->friends.end(); it++) {
        if ((*it)->login == buf) {
            this->klient->friends.erase(it);
            pthread_mutex_unlock(&this->klient->friends_mutex);
            send_info_code("410|1");
            pthread_mutex_lock(&(*it)->friends_mutex);
            for (auto it2 = (*it)->friends.begin(); it2 != (*it)->friends.end(); it2++) {
            	if ((*it2)->login == this->klient->login){
            		(*it)->friends.erase(it2);
            		pthread_mutex_unlock(&(*it)->friends_mutex);
            		return;
            	}
            }
            pthread_mutex_unlock(&(*it)->friends_mutex);
            return;
        }
    }
    pthread_mutex_unlock(&this->klient->friends_mutex);
    send_info_code("410|0");
    return;
}


void Responder::send_conv_id(string buf){
	//string login = buf
	for(int id_conv : this->klient->ID_convs){
		Conversation* c = Conversation::ALL_CONVS[id_conv];
		for(Klient* k : c->getClients()){
			if(k->login == buf){
				send_info_code("411|"+to_string(id_conv));
				return;
			}
		}
	}
	send_info_code("411|0");
	return;
}

void Responder::tell_his_friends(){
	string info = "501|"+this->klient->login+"|"+this->klient->description+"|"+this->klient->str_log()+"\n";
	const char* msg = info.c_str();
	for(Klient* k : this->klient->friends){
		if(k->logged_in)
			write(k -> socket, msg, info.length());
	}
}

bool Responder::sendMsg(Message*m, Klient* to) {
	string to_send = "500|" + m->toString1() + "\n";
	cout << to_send << endl;
	const char* msg = to_send.c_str();
	write(to->socket, msg, to_send.length());
	return true;
}

/*void Responder::check_for_unsent_msgs(){
	pthread_mutex_lock(&Message::unsent_buffer_mutex);
	for(auto const& m_k : Message::UNSENT_MSGS_BUFFER) {
		if(m_k.second->login == this->klient->login && m_k.second->logged_in) {
			sendMsg(m_k.first, m_k.second);
			Message::UNSENT_MSGS_BUFFER.erase(m_k.first);
		}
	}
	pthread_mutex_unlock(&Message::unsent_buffer_mutex);
	return;
}*/

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
