#include "pch.h"
#include "Conversation.h"


void Conversation::add_client(Klient * c){
	this->clients.push_back(c);
	return;
}

void Conversation::remove_client(Klient * c){
	this->clients.remove(c);
	return;
}

void Conversation::push_message(Message * m) {
	if (this->messages.size == MSG_HISTORY_LENGTH)
		this->messages.pop_back();
	this->messages.push_front(m);
}

Conversation::Conversation(std::list<Klient*> clients){
	this->clients = clients;
	this->ID = ALL_CONVS.size();
	ALL_CONVS[ID] = this;
}

Conversation::~Conversation(){
	for (Message* m : this->messages) {
		delete m;
	}
}
