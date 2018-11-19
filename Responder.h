#pragma once
#include <string>
#include <list>
#include "Klient.h"
using namespace std;

class Responder{
private:
	int socket;
	char buf[1000];
	list<Klient*>* clients;
	Klient* klient;

	void message(string buf);
	void login(string buf);
	void logout(string buf);
	void user_register(string buf);
	void search(string buf);
	void addFriend(string buf);
	void sendFriends(string buf);

	bool check_registration_validity(string nick, string login, string password);
	void send_info_code(string code);

public:
	void readAndRespond();
	Responder(int socket, list<Klient*>* clients);
	~Responder();
	static bool contains(string text, char* chars);
	static list<string> split_string(string text, char sep, bool msg = false);
};

