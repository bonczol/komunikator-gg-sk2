#pragma once
#include <map>
#include "Conversation.h"
class ServerSerializer
{
private:
	std::map<int, Conversation*> ALL_CONVS;
	std::map<std::string, Klient*> CLIENTS;

	friend class boost::serialization::access;
	template<class Archive>
	void serialize(Archive & ar, const unsigned int version){
		ar & ALL_CONVS;
		ar & CLIENTS;
	}
public:
	void serialize();
	void deserialize();
	ServerSerializer();
	~ServerSerializer();
};

