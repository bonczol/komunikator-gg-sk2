#pragma once
#include <string>
#include <list>
#include "Conversation.h"
#include <pthread.h>
using namespace std;

class Responder{
private:
	int socket;
	list<Klient*>* clients;
	pthread_mutex_t* clients_mutex;
	char buf[1000];
	Klient* klient;

	void message(string buf);
	void login(string buf);
	void logout(string buf);
	void user_register(string buf);
	void search(string buf);
	void addFriend(string buf);
	void sendFriends(string buf);
	void sendHistory(string buf);
	void newConv(string buf);
	void change_description(string buf);
	void delete_friend(string buf);
	
	bool check_registration_validity(string nick, string login, string password);
	void send_info_code(string code);
	bool sendMsg(Message* m, Klient* odbiorca);
	void check_for_unsent_msgs();
	
	static bool contains(string text, char* chars);
	static list<string> split_string(string text, char sep, bool msg = false);

public:
	void readAndRespond();
	Responder(int socket);
	~Responder();
	
};

