#pragma once
#include "Message.h"
#include <map>

class Conversation{
private:
	const static int MSG_HISTORY_LENGTH = 100;
	std::list<Message*> messages;
	std::list<Klient*> clients;
	int ID;

	template<class Archive>
	void serialize(Archive & ar, const unsigned int version)
	{
		ar & messages;
		ar & clients;
		ar & ID;
	}

public:
	//id konwersacji, konwersacja
	static std::map<int, Conversation*> ALL_CONVS;
	void add_client(Klient * c);
	void remove_client(Klient * c);
	void push_message(Message* m);
	int getID() { return this->ID; }
	std::list<Klient*> getClients() { return this->clients; }
	std::list<Message*> getMessages() { return this->messages; }
	//std::map<int, Conversation*> getALL_CONVS() { return Conversation::ALL_CONVS; }
	Conversation(std::list<Klient*> clients);
	~Conversation();
};

